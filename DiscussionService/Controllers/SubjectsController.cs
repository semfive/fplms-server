using System.Data;
using AutoMapper;
using DiscussionService.Contracts;
using DiscussionService.Dtos;
using DiscussionService.Models;
using Microsoft.AspNetCore.Mvc;

namespace DiscussionService.Controllers
{
    public class SubjectsController : ControllerBase
    {
        private IRepositoryWrapper _repositoryWrapper;
        private IMapper _mapper;
        public SubjectsController(IRepositoryWrapper repositoryWrapper, IMapper mapper)
        {
            _repositoryWrapper = repositoryWrapper;
            _mapper = mapper;
        }

        [HttpPost]
        public async Task<IActionResult> CreateSubject(CreateSubjectDto createSubjectDto)
        {
            try
            {
                var subject = _mapper.Map<Subject>(createSubjectDto);
                subject.Id = Guid.NewGuid();
                _repositoryWrapper.SubjectRepository.Create(subject);
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
            return StatusCode(500);
        }
    }
}