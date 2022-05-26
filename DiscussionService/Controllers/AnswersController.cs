using AutoMapper;
using DiscussionService.ActionFilters;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    [ApiController]
    [Route("api/discussion/answers")]
    public class AnswersController : ControllerBase
    {
        private IRepositoryWrapper _repositoryWrapper;
        private IMapper _mapper;
        public AnswersController(IRepositoryWrapper repositoryWrapper, IMapper mapper)
        {
            _repositoryWrapper = repositoryWrapper;
            _mapper = mapper;
        }

        [HttpPost]
        [ServiceFilter(typeof(ValidationFilterAttribute))]
        public async Task<IActionResult> CreateAnswer([FromBody] CreateAnswerDto createAnswerDto)
        {
            try
            {
                var answer = _mapper.Map<Answer>(createAnswerDto);
                answer.Id = Guid.NewGuid();
                answer.CreatedDate = DateTime.Now;
                answer.Accepted = false;
                answer.Removed = false;

                _repositoryWrapper.AnswerRepository.CreateAnswer(answer);

                await _repositoryWrapper.SaveAsync();

                return Ok();
            }
            catch (Exception ex)
            {
                return StatusCode(500, "Internal server error");
            }
        }
    }
}