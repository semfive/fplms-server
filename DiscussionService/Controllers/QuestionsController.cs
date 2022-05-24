using System.Data;
using AutoMapper;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{

    [ApiController]
    [Route("api/discussion/questions")]
    public class QuestionsController : ControllerBase
    {

        private IMapper _mapper;
        private IRepositoryWrapper _repositoryWrapper;

        public QuestionsController(IMapper mapper, IRepositoryWrapper repositoryWrapper)
        {
            _mapper = mapper;
            _repositoryWrapper = repositoryWrapper;
        }

        public async Task<IActionResult> CreateQuestion(CreateQuestionDto createQuestionDto)
        {
            try
            {
                Question question = _mapper.Map<Question>(createQuestionDto);
                question.Id = Guid.NewGuid();
                question.CreatedDate = DateTime.Now;

                _repositoryWrapper.QuestionRepository.Create(question);
                await _repositoryWrapper.SaveAsync();
                return Ok();
            }
            catch (DataException)
            {
                Console.WriteLine("Unable to save data");
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex);
            }
            return StatusCode(500, "Internal server error");
        }

    }
}